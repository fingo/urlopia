import {Badge} from "react-bootstrap";

import styles from "./ClickablePill.module.scss"

export const ClickablePill = ({children, onClick}) => {
    return (
        <Badge pill onClick={onClick} className={styles.clickablePill}>
            {children}
        </Badge>
    )
}