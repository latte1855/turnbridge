// 繁中註解：示範 999 明細「不拆單」切檔，並以 multipart 上傳
import fs from "fs";
import path from "path";
import axios from "axios";
import FormData from "form-data";
import { parse } from "fast-csv";

const API = process.env.API_BASE || "https://turnbridge.local/api/v1";
const TOKEN = process.env.TOKEN || "REPLACE_ME";
const MAX = 999;

/** 載入 CSV 並以「明細」為單位切檔，遇到同一張發票跨界時整張移到下一檔 */
async function splitCsvNotBreakingInvoice(inPath) {
  const rows = await new Promise((resolve, reject) => {
    const r = [];
    fs.createReadStream(inPath)
      .pipe(parse({ headers: true }))
      .on("error", reject)
      .on("data", row => r.push(row))
      .on("end", () => resolve(r));
  });

  const buckets = [];
  let bucket = [];
  let currentInvoiceNo = null;

  for (const row of rows) {
    const inv = row.InvoiceNo || row.invoiceNo || row["Invoice No"];
    const isSameInvoice = currentInvoiceNo && inv === currentInvoiceNo;

    if (bucket.length >= MAX && !isSameInvoice) {
      buckets.push(bucket);
      bucket = [];
    }
    if (!isSameInvoice && bucket.length >= MAX) {
      buckets.push(bucket);
      bucket = [];
    }
    bucket.push(row);
    currentInvoiceNo = inv;
  }
  if (bucket.length > 0) buckets.push(bucket);
  return buckets;
}

/** 以上傳 API 進行測試（回傳 importId） */
async function uploadCsvRows(rows, splitSeq = 1) {
  const tmp = path.join(process.cwd(), `tmp_${Date.now()}_${splitSeq}.csv`);
  const headers = Object.keys(rows[0] || {});

  const csv = [headers.join(",")]
    .concat(rows.map(r => headers.map(h => (r[h] ?? "")).join(",")))
    .join("\n");
  fs.writeFileSync(tmp, csv);

  const fd = new FormData();
  fd.append("file", fs.createReadStream(tmp));
  fd.append("md5", "");
  fd.append("encoding", "UTF-8");
  fd.append("profile", "default");

  const { data } = await axios.post(`${API}/upload/invoice`, fd, {
    headers: { Authorization: `Bearer ${TOKEN}`, ...fd.getHeaders() },
    maxBodyLength: Infinity
  });
  fs.unlinkSync(tmp);
  return data.id || data.importId;
}

(async () => {
  const source = "tools/agent-tests/node/samples/invoice_legacy_A0401.csv";
  const buckets = await splitCsvNotBreakingInvoice(source);
  console.log(`切成 ${buckets.length} 檔：`, buckets.map(b => b.length));

  let seq = 1;
  for (const b of buckets) {
    const importId = await uploadCsvRows(b, seq++);
    console.log("上傳成功 importId =", importId);
  }
})();
