import {useState} from "react";
import {Button, Form, FormControl} from "react-bootstrap";
import {useHistory} from "react-router-dom";

import {login} from "../../api/services/session.service"
import {useAppInfo} from "../../contexts/app-info-context/appInfoContext";
import {pushSuccessNotification} from "../../helpers/notifications/Notifications";
import styles from "./LoginForm.module.scss"

const LOADING_MESSAGE = "...";
const LOGIN_MESSAGE = "Zaloguj się";

export const LoginForm = () => {

    const [isLoading, setLoading] = useState(false);
    const [mail, setMail] = useState("");
    const [password, setPassword] = useState("");
    const history = useHistory();

    const [appInfoState,] = useAppInfo()
    const {version, commitId} = appInfoState.appInfo

    const handleFormSubmit = event => {
        event.preventDefault();
        setLoading(true);
        const credentials = {
            mail,
            password,
        }
        login(credentials).then(
            () => {
                pushSuccessNotification("Logowanie zakończone sukcesem")
                history.push("/");
                history.go(0);
            },
            () => {
                setLoading(false);
            }
        );
    }

    return (
        <Form className={styles.loginForm} onSubmit={event => handleFormSubmit(event)}>
            <div className={styles.formContainer}>
                <h2 className={styles.title}>Urlopia</h2>
                <div className={styles.inputContainer}>
                    <FormControl value={mail} onChange={event => setMail(event.target.value)}
                                 type="email" placeholder="Email" className={styles.input}/>
                    <FormControl value={password} onChange={event => setPassword(event.target.value)}
                                 type="password" placeholder="Hasło" className={styles.input}/>
                </div>
                <Button className={styles.button} type="submit" data-testid="login-btn">
                    {isLoading ? LOADING_MESSAGE : LOGIN_MESSAGE}</Button>
                <span className={styles.versionContainer}>
                    {`${version} ${commitId}`}
                </span>
            </div>
        </Form>
    );
}