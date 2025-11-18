import tenant from 'app/entities/tenant/tenant.reducer';
import importFile from 'app/entities/import-file/import-file.reducer';
import importFileLog from 'app/entities/import-file-log/import-file-log.reducer';
import invoice from 'app/entities/invoice/invoice.reducer';
import invoiceItem from 'app/entities/invoice-item/invoice-item.reducer';
import invoiceAssignNo from 'app/entities/invoice-assign-no/invoice-assign-no.reducer';
import turnkeyMessage from 'app/entities/turnkey-message/turnkey-message.reducer';
import webhookEndpoint from 'app/entities/webhook-endpoint/webhook-endpoint.reducer';
import webhookDeliveryLog from 'app/entities/webhook-delivery-log/webhook-delivery-log.reducer';
import manualAction from 'app/entities/manual-action/manual-action.reducer';
import importFileItem from 'app/entities/import-file-item/import-file-item.reducer';
import importFileItemError from 'app/entities/import-file-item-error/import-file-item-error.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  tenant,
  importFile,
  importFileLog,
  invoice,
  invoiceItem,
  invoiceAssignNo,
  turnkeyMessage,
  webhookEndpoint,
  webhookDeliveryLog,
  manualAction,
  importFileItem,
  importFileItemError,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
