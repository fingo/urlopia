//using gdc algorithm here
export const simplifyWorkTime = (workTime: number) => {
    if (workTime === 8) {
        return `1`
    }
    let gdc = workTime;
    let b = 8
    let c;
    while (b) {
        c = gdc % b;
        gdc = b;
        b = c;
    }
    return `${workTime / gdc}/${8 / gdc}`;
}
