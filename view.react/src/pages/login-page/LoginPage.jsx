import Logo from '../../assets/logo.png'
import {LoginForm} from "../../components/login-form/LoginForm";
import styles from "./LoginPage.module.scss"

export const LoginPage = () => {
    return (
        <div className={styles.container}>
            <h1>
                <img src={Logo} alt="FINGO" className={styles.logo}/>
            </h1>
            <div className={styles.loginFormContainer}>
                <LoginForm/>
            </div>
        </div>
    );
}