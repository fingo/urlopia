import PropTypes from 'prop-types';
import {NavLink} from 'react-router-dom';

import styles from './Link.module.scss';

export const Link = ({
    to,
    onClick,
    children,
}) => {
    return (
        <NavLink to={to} activeClassName={styles.activeLink} onClick={onClick}>
            {children}
        </NavLink>
    )
}

Link.propTypes = {
    to: PropTypes.string.isRequired,
    onClick: PropTypes.func,
}

Link.defaultProps = {
    onClickLinkOrOutside: () => null,
}
