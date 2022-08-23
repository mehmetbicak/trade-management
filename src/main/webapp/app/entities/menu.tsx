import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/exception-message">
        <Translate contentKey="global.menu.entities.exceptionMessage" />
      </MenuItem>
    </>
  );
};

export default EntitiesMenu as React.ComponentType<any>;
