import {OverlayTrigger, Popover} from "react-bootstrap";
import {InfoCircleFill as InfoIcon} from "react-bootstrap-icons";

import styles from './InfoOverlay.module.scss'

export const InfoOverlay = () => {
    return (
        <OverlayTrigger
            trigger={['hover', 'focus']}
            placement='bottom'
            overlay={
                <Popover id='popover'>
                    <Popover.Body>
                        <b>2 dni przysługuje w związku z:</b>
                        <ul>
                            <li>Twoim ślubem,</li>
                            <li>narodzinami dziecka,</li>
                            <li>zgonem i pogrzebem małżonka, dziecka, ojca, matki, ojczyma lub
                                macochy.
                            </li>
                        </ul>
                        <br/>
                        <b>1 dzień przysługują w razie:</b>
                        <ul>
                            <li>ślubu Twojego dziecka,</li>
                            <li>zgonu i pogrzebu siostry, brata, teściowej, teścia, babki, dziadka, a
                                także innej osoby, którą utrzymujesz lub którą się bezpośrednio
                                opiekujesz.
                            </li>
                        </ul>
                    </Popover.Body>
                </Popover>
            }
        >
            <InfoIcon className={styles.infoIcon}/>
        </OverlayTrigger>

    )
}