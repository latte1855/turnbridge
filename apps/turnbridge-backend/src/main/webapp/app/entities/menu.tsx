import React from 'react';
import { Translate } from 'react-jhipster'; // eslint-disable-line

import MenuItem from 'app/shared/layout/menus/menu-item'; // eslint-disable-line

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/upload-job">
        <Translate contentKey="global.menu.entities.uploadJob" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/upload-job-item">
        <Translate contentKey="global.menu.entities.uploadJobItem" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/stored-object">
        <Translate contentKey="global.menu.entities.storedObject" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/track-range">
        <Translate contentKey="global.menu.entities.trackRange" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
