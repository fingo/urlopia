export const hoursChangeMapper = (cell, workTime) => {
    if (Math.abs(cell) < workTime) {
        return cell + "h";
    }
    let days = Math.trunc(cell / workTime);
    let hours = Math.abs(cell%workTime);
    return (days + "d " + hours + "h");
}

export const hoursChangeColorMapper = (cell) => {
    let color = "yellow";
    switch(true) {
        case cell > 0:
            color = "green"
            break;
        case cell < 0:
            color = "red"
            break;
        default:
            break;
    }
    return {color: color, verticalAlign: 'middle'};
}