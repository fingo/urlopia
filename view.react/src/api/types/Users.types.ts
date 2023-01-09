export interface UserOutput {
    fullName: string,
    userId: number,
    mailAddress: string,
    teams: string[],
    workingHours: number,
    workTime: {
        numerator: number,
        denominator: number
    }
}