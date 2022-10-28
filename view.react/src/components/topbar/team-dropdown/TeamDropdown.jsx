import PropTypes from 'prop-types';
import {Dropdown} from 'react-bootstrap';
import {PersonCircle} from "react-bootstrap-icons";

import styles from './TeamDropdown.module.scss';

export const TeamDropdown = ({
    userName,
    teams,
}) => {
    return (
        <Dropdown align="end">
            <Dropdown.Toggle className={styles.dropdown} bsPrefix="p-0" variant="link">
                <span>
                    <PersonCircle className={styles.icon}/>
                </span>
                <p className="d-none d-lg-flex">{userName}</p>
            </Dropdown.Toggle>

            <Dropdown.Menu data-testid="team-dropdown">
                <Dropdown.Header className="d-lg-none text-center">{userName}</Dropdown.Header>
                <Dropdown.Divider className="m-0 d-lg-none"/>

                {
                    teams?.length ?
                        teams.map(({name, leader}, i) =>
                           <div key={i}
                            className={i === teams.length - 1 ? styles.teamInfo : styles.teamInfoWithDivider}
                       >
                           <p className={styles.team}>{name}</p>
                           <p className={styles.leader}>Lider: {leader}</p>
                       </div>
                        )

                        :

                        <Dropdown.Header className="text-center">Brak zespołów</Dropdown.Header>
                }
            </Dropdown.Menu>
        </Dropdown>
    );
}

TeamDropdown.propTypes = {
    userName: PropTypes.string.isRequired,
    teams: PropTypes.arrayOf(
        PropTypes.shape({
            name: PropTypes.string,
            leader: PropTypes.string,
        })).isRequired,
}