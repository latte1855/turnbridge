import React from 'react';
import { Translate } from 'react-jhipster';
import { Link } from 'react-router-dom';
import { NavItem, NavLink } from 'reactstrap';

export const ImportMonitorMenu = () => (
  <NavItem>
    <NavLink tag={Link} to="/import-monitor" className="d-flex align-items-center">
      <span className="fa fa-file-import me-1" />
      <span>
        <Translate contentKey="global.menu.import.monitor" />
      </span>
    </NavLink>
  </NavItem>
);
