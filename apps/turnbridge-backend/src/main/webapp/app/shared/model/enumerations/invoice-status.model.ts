export enum InvoiceStatus {
  DRAFT = '草稿',

  NORMALIZED = '已正規化',

  PENDING_XML = '待產 XML',

  IN_PICKUP = 'Turnkey 處理中',

  ACKED = 'ACK',

  ERROR = 'ERROR',

  VOIDED = '作廢',
}
