export const calendarKeys = {
  all: ["dataSource"] as const,
  lists: () => [...calendarKeys.all, "list"] as const,
  list: ({ startDate, endDate }: { startDate: string; endDate: string }) =>
    [...calendarKeys.lists(), { startDate, endDate }] as const,
};
