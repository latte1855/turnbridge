import React from 'react';
import { Translate } from 'react-jhipster'; // eslint-disable-line

import MenuItem from 'app/shared/layout/menus/menu-item'; // eslint-disable-line

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/tenant">
        <Translate contentKey="global.menu.entities.tenant" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/import-file">
        <Translate contentKey="global.menu.entities.importFile" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/import-file-log">
        <Translate contentKey="global.menu.entities.importFileLog" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/invoice">
        <Translate contentKey="global.menu.entities.invoice" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/invoice-item">
        <Translate contentKey="global.menu.entities.invoiceItem" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/invoice-assign-no">
        <Translate contentKey="global.menu.entities.invoiceAssignNo" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/turnkey-message">
        <Translate contentKey="global.menu.entities.turnkeyMessage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/webhook-endpoint">
        <Translate contentKey="global.menu.entities.webhookEndpoint" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/webhook-delivery-log">
        <Translate contentKey="global.menu.entities.webhookDeliveryLog" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/manual-action">
        <Translate contentKey="global.menu.entities.manualAction" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
