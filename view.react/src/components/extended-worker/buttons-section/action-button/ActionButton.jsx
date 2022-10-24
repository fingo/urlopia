import classNames from "classnames";
import PropTypes from "prop-types";
import {Button, OverlayTrigger, Tooltip} from "react-bootstrap";

import btnStyles from "../../../../global-styles/btn.module.scss";
import {AttentionIcon, TextWithIcon} from "../../../../helpers/icons/Icons";
import styles from './ActionButton.module.scss';

export const ActionButton = ({tooltipText, icon, onButtonClick, modal, showModal, isWithNotification}) => {
    const buttonClass = classNames(btnStyles.btnClass, {[styles.notifyButton]: isWithNotification});
    return (
        <>
            <OverlayTrigger
                placement='bottom'
                overlay={
                    <Tooltip id={`tooltip-${tooltipText}`}>
                        {tooltipText}
                    </Tooltip>
                }
            >
                <Button className={buttonClass}
                        onClick={(e) => {
                            e.currentTarget.blur();
                            onButtonClick(true);
                        }}
                >
                    {icon}
                    {
                        isWithNotification &&
                        <div className={styles.notifyIcon}>
                            <TextWithIcon
                                text=''
                                icon={<AttentionIcon/>}
                                showIcon={true}
                            />
                        </div>
                    }
                </Button>
            </OverlayTrigger>
            {showModal && modal}
        </>
    )
}

ActionButton.propTypes = {
    tooltipText: PropTypes.string,
    icon: PropTypes.object.isRequired,
    onButtonClick: PropTypes.func.isRequired,
    modal: PropTypes.object.isRequired,
    showModal: PropTypes.bool,
    isWithNotification: PropTypes.bool,
}

ActionButton.defaultProps = {
    tooltipText: 'default-tooltip',
    showModal: false,
    isWithNotification: false,
}
