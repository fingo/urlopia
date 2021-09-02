export const parseDaysPool = (daysToAdd) => {
    const regex = /^ *[+-]?( *\d+d)?( *\d+h)?( *\d+m)? *$/;
    if (!regex.test(daysToAdd)) {
        return false;
    }

    let parsedPart = new RegExp("(\\d+)d").exec(daysToAdd);
    let hours = parsedPart ? parseInt(parsedPart[1]) * 8 : 0;

    parsedPart = new RegExp("(\\d+)h").exec(daysToAdd);
    hours += parsedPart ? parseInt(parsedPart[1]) : 0;

    parsedPart = new RegExp("(\\d+)m").exec(daysToAdd);
    hours += parsedPart ? parseInt(parsedPart[1]) / 60 : 0;

    if (new RegExp("-").test(daysToAdd)) {
        hours *= -1;
    }

    return hours;
}