import SaveAsIcon from "@mui/icons-material/SaveAs";
import UndoIcon from "@mui/icons-material/Undo";
import {IconButton, TextField} from "@mui/material";
import React, {useState} from "react";

import {IAutomaticVacationDaysRow} from "../../../api/types/AutomaticVacationDays.types";


interface IProps {
    value: string,
    name: string,
    pattern: string,
    helperText: string,
    row: any,
    saveRow: (arg0: IAutomaticVacationDaysRow) => void
}

export const TextFieldWrapper = ({value, name, pattern, helperText, row, saveRow}: IProps) => {

    const [inputValue, setInputValue] = useState(value);
    const [isInputValid, setIsInputValid] = useState(true)

    const getHelperMessage = () => {
        return isInputValid ? '' : helperText;
    }

    const onChange = (e: React.ChangeEvent<HTMLInputElement>) : void => {
        const eventValue = e.target.value;
        const isValid = new RegExp(pattern).test(eventValue)
        setInputValue(eventValue)
        setIsInputValid(isValid)
    }

    const onSave = () => {
        const updatedRow = {...row, [name]: inputValue};
        saveRow(updatedRow)
    }

    const onRevert = () => {
        setInputValue(value)
        setIsInputValid(true)
    }

    const inputChange = value !== inputValue
    const renderSaveButton = inputChange && isInputValid

    return <>
        {inputChange && <IconButton
            aria-label="revert"
            onClick={onRevert}
        >
            <UndoIcon />
        </IconButton>}
        <TextField value={inputValue}
                   error={!isInputValid}
                   name={name}
                   onChange={onChange}
                   helperText={getHelperMessage()}
                   inputProps={{pattern: pattern}}
        />
        {renderSaveButton && <IconButton
            aria-label="save"
            onClick={onSave}
        >
            <SaveAsIcon />
        </IconButton>}
    </>

}