export enum UploadJobStatus {
  RECEIVED = '已接收',

  PARSING = '解析中',

  VALIDATING = '驗證中',

  PACKING = '打包中（MIG/XML）',

  SENT = '已送 Turnkey',

  RESULT_READY = '回饋已備妥',

  FAILED = '失敗（需人工或重送）',
}
