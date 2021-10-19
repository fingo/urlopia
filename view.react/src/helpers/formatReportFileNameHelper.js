export const formatReportFileName = (fullName, year) => {
    fullName = fullName.split(' ').reverse().join('');
    return `ewidencja_czasu_pracy_${year}_${fullName}`;
}