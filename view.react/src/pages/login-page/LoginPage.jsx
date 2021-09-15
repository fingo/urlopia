import Logo from '../../assets/logo.png'
import {LoginForm} from "../../components/login-form/LoginForm";
import {useAppInfo} from "../../contexts/app-info-context/appInfoContext";
import styles from "./LoginPage.module.scss"

export const LoginPage = () => {
    const [appInfoState,] = useAppInfo()
    const {version, commitId} = appInfoState.appInfo

    return (
        <div className={styles.container}>
            <h1>
                <img src={Logo} alt="FINGO" className={styles.logo}/>
            </h1>
            <div className={styles.loginFormContainer}>
                <LoginForm/>
            </div>
            <div className={styles.versionContainer}>
                {`${version} ${commitId}`}
            </div>
        </div>
    );
}