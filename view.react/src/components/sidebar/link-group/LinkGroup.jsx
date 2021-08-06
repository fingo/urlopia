import PropTypes from "prop-types";
import {useEffect, useState} from "react";
import {Nav} from 'react-bootstrap';
import {useLocation} from "react-router-dom";

import styles from './LinkGroup.module.scss';

export const LinkGroup = ({
    name,
    children,
}) => {
    const location = useLocation();
    const [isActive, setIsActive] = useState(false);

    useEffect(() => {
        for (let i = 0; i < children.length; i++) {
            if (children[i].props.to.startsWith(location.pathname)) {
                setIsActive(true);
                break;
            }
            else {
                setIsActive(false);
            }
        }
    }, [location, children])

    return (
        <>
            <Nav.Link
                className={isActive ? styles.disabledLinkActive : styles.disabledLink}
                disabled
            >
                {name}
            </Nav.Link>

            <div className={styles.subLinks}>
                {children}
            </div>
        </>
    );
}

LinkGroup.propTypes = {
    name: PropTypes.string.isRequired,
}
