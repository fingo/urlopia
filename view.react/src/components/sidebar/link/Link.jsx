import PropTypes from 'prop-types';
import {NavLink} from 'react-router-dom';

import styles from './Link.module.scss';

export const Link = ({
    to,
    onClick,
    children,
    testId,
}) => {
    return (
        <NavLink to={to} activeClassName={styles.activeLink} onClick={onClick} data-testid={testId}>
            {children}
        </NavLink>
    )
}

Link.propTypes = {
    to: PropTypes.string.isRequired,
    onClick: PropTypes.func,
    testId:PropTypes.string,
}

Link.defaultProps = {
    onClickLinkOrOutside: () => null,
}
