import {FormControl, InputLabel, MenuItem, Select, SelectChangeEvent} from "@mui/material";
import React from "react";

import useGetUsers from "../../../api/queryHooks/queries/User/useGetUsers";
import {NO_AUTH_USER_KEY, USER_DATA_KEY} from "../../../constants/session.keystorage";

export const NoAuthUsersDropdown = () => {

    const handleChange = (event: SelectChangeEvent) => {
        const userId = event.target.value as string;
        localStorage.setItem(NO_AUTH_USER_KEY, userId)
        localStorage.removeItem(USER_DATA_KEY)
        window.location.href = window.location.origin
    };

    const {data: users} = useGetUsers({active: true});

    return (
        <FormControl fullWidth>
            <InputLabel
                id="no-auth-user-selector-label"
                sx = {{
                    color: "#002900",
                    fontSize: '1.3rem',
                    "&.Mui-focused": {
                        color: "white",
                    },
                }}>Wybierz użytkownika</InputLabel>
            <Select
                labelId="no-auth-user-selector-label"
                id="no-auth-user-selector"
                label="Wybierz użytkownika"
                onChange={handleChange}
                sx = {{
                    fontSize: '1.3rem',
                    color: '#002900',
                    "&:after": {
                        borderBottomColor: "white",
                    },
                    "& .MuiSvgIcon-root": {
                        color: "#002900",
                    },
                    boxShadow: 'none', '.MuiOutlinedInput-notchedOutline': { border: 0 },
                    "&.Mui-focused": {
                        boxShadow: 'none', '.MuiOutlinedInput-notchedOutline': { border: 0 }
                    },
                }}
            >
                {
                    users && users.map(user => {
                        return <MenuItem key={user.userId}
                                         value={user.userId}>
                            {user.fullName}
                        </MenuItem>
                    })
                }
            </Select>
        </FormControl>
    );
}