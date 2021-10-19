import {Formik} from "formik";
import PropTypes from "prop-types";
import {Button, Form, InputGroup} from "react-bootstrap";
import {CheckLg as AcceptIcon} from "react-bootstrap-icons";
import * as yup from "yup";

import styles from "./ChangeDaysPoolForm.module.scss";

const INCORRECT_FORMAT_MSG = 'Niepoprawny format!';
const EMPTY_INPUT_MSG = 'Podaj pulę dni!';

export const ChangeDaysPoolForm = ({onSubmit}) => {
    const schema = yup.object().shape({
        daysToChange: yup.string()
            .matches(/^ *[+-]?( *\d+d)?( *\d+h)?( *\d+m)? *$/, INCORRECT_FORMAT_MSG)
            .required(EMPTY_INPUT_MSG),
        comment: yup.string(),
    });

    return (
        <Formik
            validationSchema={schema}
            onSubmit={(values, {resetForm}) => {
                onSubmit(values);
                resetForm();
            }}
            initialValues={{
                daysToChange: '',
                comment: '',
            }}
        >
            {({
                  handleSubmit,
                  handleChange,
                  values,
                  errors,
              }) => (
                <Form className="d-grid"
                      noValidate
                      onSubmit={(e, formikFun) => handleSubmit(e, formikFun)}
                >
                    <Form.Label><strong>Zmień pulę urlopu (dni)</strong></Form.Label>
                    <InputGroup hasValidation>
                        <Form.Control type="text"
                                      className={styles.changeDaysPoolInput}
                                      placeholder="1d 5h 40m"
                                      name='daysToChange'
                                      value={values.daysToChange}
                                      onChange={handleChange}
                                      isInvalid={!!errors.daysToChange}
                        />
                        <Form.Control.Feedback type="invalid" tooltip>{errors.daysToChange}</Form.Control.Feedback>
                    </InputGroup>
                    <Form.Control as="textarea"
                                  className={styles.commentInput}
                                  placeholder='Komentarz...'
                                  name='comment'
                                  value={values.comment}
                                  onChange={handleChange}
                    />
                    <Button type="submit"
                            className={styles.changeDaysPoolBtn}
                            variant='outline-success'
                            onClick={e => e.currentTarget.blur()}
                    >
                        <AcceptIcon/>
                    </Button>
                </Form>
            )}
        </Formik>
    );
}

ChangeDaysPoolForm.propTypes = {
    onSubmit: PropTypes.func.isRequired,
}