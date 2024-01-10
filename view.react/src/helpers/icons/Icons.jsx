import {CircleFill} from "react-bootstrap-icons";

import styles from './Icons.module.scss';

export const AttentionIcon = () => {
    return <CircleFill
        className={styles.newEventDot}
    />
}

export const TextWithIcon = ({text, icon, showIcon}) => {
    return (
        <span>
            {text}
            {showIcon && icon}
        </span>
    )
}