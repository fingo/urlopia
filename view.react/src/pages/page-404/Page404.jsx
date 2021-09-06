import {Button} from 'react-bootstrap';
import {useHistory} from 'react-router-dom';

import img from '../../assets/img.png'
import styles from "./Page404.module.scss";

export const URL = "/404";

export const Page404 = () => {

    const history = useHistory();

    return (
        <>
            <div className={styles.container}>
                <img src={img} alt="404"/>
                <h3>Wygląda na to, że znajdujesz się w niewłaściwym miejscu...</h3>
                <Button className={styles.btnClass} onClick={() => history.push("/")} data-testid="return-btn">Powrót</Button>
            </div>
        </>
    );
};