export const hoursChangeMapper = (cell, workTime) => {
    if(typeof cell == 'number') {
        if (workTime === 8) {
            if (Math.abs(cell) < workTime) {
                return cell + "h";
            }
            let days = Math.trunc(cell / workTime);
            let hours = Math.abs(cell % workTime);
            return (days + "d " + hours + "h");
        }
        return (cell + "h");
    }
    return cell;
}
