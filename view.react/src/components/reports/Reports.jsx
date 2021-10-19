import {useState} from 'react';
import {Button} from "react-bootstrap";

import {EvidenceReportModal} from "./evidence-report-modal/EvidenceReportModal";
import {PresenceListModal} from "./presence-list-modal/PresenceListModal";
import styles from './Reports.module.scss';

export const Reports = () => {

    const [showEvidenceModal, setShowEvidenceModal] = useState(false);
    const [showPresenceModal, setShowPresenceModal] = useState(false);

    return (
        <>
            <div className={styles.container}>
                <Button className={styles.btnClass} onClick={() => setShowEvidenceModal(true)}>
                    Ewidencja czasu pracy
                </Button>
                <Button className={styles.btnClass} onClick={() => setShowPresenceModal(true)}>
                    Miesięczna lista obecności
                </Button>
            </div>
            <EvidenceReportModal
                show={showEvidenceModal}
                onHide={()=>setShowEvidenceModal(false)}
            />
            <PresenceListModal
                show={showPresenceModal}
                onHide={()=>setShowPresenceModal(false)}
            />
        </>
    )
}