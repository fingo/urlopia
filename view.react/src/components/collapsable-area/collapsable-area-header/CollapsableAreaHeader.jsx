import classNames from "classnames";
import PropTypes from 'prop-types';
import {useAccordionButton} from "react-bootstrap";
import {Card} from "react-bootstrap";
import {ChevronDown as ArrowDownIcon, ChevronRight as ArrowRightIcon} from "react-bootstrap-icons";

import styles from "./CollapsableAreaHeader.module.scss";

export const CollapsableAreaHeader = ({
    eventKey,
    onClick,
    isActive,
    children,
}) => {
    const decoratedOnClick = useAccordionButton(eventKey, () => onClick());

    const btnClass = classNames(styles.btn, {[styles.btnActive]: isActive});
    return (
        <button
            type="button"
            className={btnClass}
            onClick={decoratedOnClick}
        >
            <Card.Header className={styles.header}>
                {
                    isActive
                        ?
                        <ArrowDownIcon className='me-3'/>
                        :
                        <ArrowRightIcon className='me-3'/>
                }
                {children}
            </Card.Header>
        </button>
    );
}

CollapsableAreaHeader.propTypes = {
    eventKey: PropTypes.string.isRequired,
    onClick: PropTypes.func.isRequired,
    isActive: PropTypes.bool.isRequired,
}