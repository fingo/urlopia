import { QueryClient } from '@tanstack/react-query';

export default new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 0, // Maybe someday it will be useful, but shouldn't retry on some errors (like 404)
      cacheTime: 0, // TODO: We should use this cache but only after we ensure that we're invalidating queries (e.g. when requesting vacation)
    },
  },
});
