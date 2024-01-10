import 'animate.css/animate.min.css';

import {CheckCircle, ExclamationCircle} from "react-bootstrap-icons";
import { store } from 'react-notifications-component'

import styles from './Notifications.module.scss'

export const pushSuccessNotification = message => {
    pushNotification(successNotification({message}))
}

export const pushErrorNotification = message => {
    pushNotification(errorNotification({message}))
}

export const pushNotification = notification => {
    store.addNotification(notification)
}

export const genericNotification = ({title, message, type}) => {
    return {
        title,
        message,
        type,
        insert: "bottom",
        container: "bottom-right",
        animationIn: ["animate__animated", "animate__fadeInRight"],
        animationOut: ["animate__animated", "animate__fadeOut"],
        dismiss: {
            duration: 3000,
            pauseOnHover: true
        }
    }
}

export const successNotification = ({title = "Sukces", message}) => {
    return genericNotification({
        message: <SuccessMessage title={title} message={message} />,
        type: "success"
    })
}

const SuccessMessage = ({title, message}) => (
    <MessageWithIcon
        title={title}
        message={message}
        icon={CheckCircle}
    />
)

export const errorNotification = ({title = "Błąd", message}) => {
    return genericNotification({
        message: <ErrorMessage title={title} message={message} />,
        type: "danger"
    })
}

const ErrorMessage = ({title, message}) => (
    <MessageWithIcon
        title={title}
        message={message}
        icon={ExclamationCircle}
    />
)

const MessageWithIcon = ({title, message, icon}) => {
    const Icon = icon
    return (
        <div className={styles.mainContainer}>
            <div className={styles.iconContainer}>
                <Icon size={32} />
            </div>
            <div className={styles.contentContainer}>
                <div className={styles.title}>
                    {title}
                </div>
                <div className={styles.message}>
                    {message}
                </div>
            </div>
        </div>
    )
}