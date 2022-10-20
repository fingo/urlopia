interface IAbsentUser {
  userName: string;
  teams: string[];
}

interface IPresenceConfirmation {
  startTime: string|null;
  endTime: string|null;
  confirmed: boolean;
}

interface ICurrentUserInformation {
  absent: boolean;
  vacationHoursModifications: unknown[];
  presenceConfirmation: IPresenceConfirmation;
}

interface ICalendarEntry {
  holidays: string[];
  workingDay: boolean;
  absentUsers: IAbsentUser[];
  currentUserInformation: ICurrentUserInformation;
}

export interface ICalendarResponse {
  calendar: {
    [key: string]: ICalendarEntry;
  }
}