import React from "react";
import {BeatLoader} from "react-spinners";

import styles from '../../global-styles/loading-spinner.module.scss';

interface ISpinnerProps {
    waitMessage: string
}

export const Spinner = ({waitMessage} :ISpinnerProps) => {
    return(
        <div className={styles.spinner}>
            <BeatLoader color='#78A612' size={50}/>
            <h1>{waitMessage}</h1>
        </div>
        )
}
