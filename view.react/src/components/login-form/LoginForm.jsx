import {useState} from "react";
import {Button, Form, FormControl} from "react-bootstrap";
import { useHistory } from "react-router-dom";

import {login} from "../../api/services/session.service"
import styles from "./LoginForm.module.scss"

const LOADING_MESSAGE = "...";
const LOGIN_MESSAGE = "Zaloguj się";

export const LoginForm = () => {

    const [isLoading, setLoading] = useState(false);
    const [showAlert, setShowAlert] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const history = useHistory();

    const handleFormSubmit = event => {
        event.preventDefault();
        setShowAlert(false);
        setLoading(true);
        const credentials = {
            email,
            password
        }
        login(credentials).then(
            () => {
                history.push("/");
                window.location.reload();
            },
            error => {
                setErrorMessage(error.message)
                setShowAlert(true);
                setLoading(false);
            }
        );
    }

    return (
        <Form className={styles.loginForm} onSubmit={event => handleFormSubmit(event)}>
            <div className={styles.formContainer}>
                <h2 className={styles.title}>Urlopia</h2>
                <div className={styles.inputContainer}>
                    <FormControl value={email} onChange={event => setEmail(event.target.value)}
                                 type="email" placeholder="Email" className={styles.input}/>
                    <FormControl value={password} onChange={event => setPassword(event.target.value)}
                                 type="password" placeholder="Hasło" className={styles.input}/>
                </div>
                <Button className={styles.button} type="submit">
                    {isLoading ? LOADING_MESSAGE : LOGIN_MESSAGE}</Button>
            </div>
            <div className={styles.alertContainer}>
                <strong>{showAlert && errorMessage}</strong>
            </div>
        </Form>
    );
}