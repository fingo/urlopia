export const calendarKeys = {
  all: ["calendar"] as const,
  lists: () => [...calendarKeys.all, "list"] as const,
  list: ({ startDate, endDate }: { startDate: string; endDate: string }) =>
    [...calendarKeys.lists(), { startDate, endDate }] as const,
};

export const automaticVacationDays = {
  all: ["automaticVacationDays"] as const,
  lists: () => [...automaticVacationDays.all, "list"] as const
}

export const userKeys = {
  all: ["user"] as const,
  lists: () => [...userKeys.all, "list"] as const,
  list: ({ active }: { active: boolean }) =>
      [...userKeys.lists(), { active }] as const,
}