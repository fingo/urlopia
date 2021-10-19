import PropTypes from 'prop-types';
import {useState} from "react";
import {Accordion, Card} from "react-bootstrap";

import {CollapsableAreaHeader} from "./collapsable-area-header/CollapsableAreaHeader";
import styles from './CollapsableArea.module.scss';

export const CollapsableArea = ({
    title,
    children,
    onOpen,
    shouldBeCollapsed
}) => {
    const [isCollapsed, setIsCollapsed] = useState(shouldBeCollapsed);

    const handleCollapse = () => {
        if (isCollapsed) {
            onOpen()
        }
        setIsCollapsed(!isCollapsed);
    }

    return (
        <Accordion defaultActiveKey={shouldBeCollapsed ? "" : "0"}>
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
    shouldBeCollapsed: PropTypes.bool
}

CollapsableArea.defaultProps = {
    onOpen: () => {},
    shouldBeCollapsed: true
}