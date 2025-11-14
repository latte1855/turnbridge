// 繁中註解：HMAC 簽章工具（Webhook/上傳驗證用）
import CryptoJS from "crypto-js";

/**
 * 依據系統規範，以 HMAC-SHA256 對 body 計算簽章。
 * @param {string} secret 共享密鑰
 * @param {string|Buffer} body 原文
 * @returns {string} base64 字串
 */
export function hmacSha256Base64(secret, body) {
  const hash = CryptoJS.HmacSHA256(body, secret);
  return CryptoJS.enc.Base64.stringify(hash);
}
