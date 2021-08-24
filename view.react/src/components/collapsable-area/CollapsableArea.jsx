import PropTypes from 'prop-types';
import {useState} from "react";
import {Accordion, Card} from "react-bootstrap";

import {CollapsableAreaHeader} from "./collapsable-area-header/CollapsableAreaHeader";
import styles from './CollapsableArea.module.scss';

export const CollapsableArea = ({
    title,
    children,
    onOpen
}) => {
    const [isCollapsed, setIsCollapsed] = useState(true);

    const handleCollapse = () => {
        if (isCollapsed) {
            onOpen()
        }
        setIsCollapsed(!isCollapsed);
    }

    return (
        <Accordion>
            <Card className={styles.main}>
                <CollapsableAreaHeader
                    eventKey="0"
                    onClick={handleCollapse}
                    isActive={!isCollapsed}
                >
                    {title}
                </CollapsableAreaHeader>
                <Accordion.Collapse eventKey="0">
                    <Card.Body className={styles.card}>{children}</Card.Body>
                </Accordion.Collapse>
            </Card>
        </Accordion>
    );
};

CollapsableArea.propTypes = {
    title: PropTypes.oneOfType([PropTypes.string, PropTypes.object]).isRequired,
    onOpen: PropTypes.func,
}

CollapsableArea.defaultProps = {
    onOpen: () => {},
}