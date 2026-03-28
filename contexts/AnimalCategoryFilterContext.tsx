import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';

import type { AnimalCategoryFilter } from '@/constants/animalCategory';
import { getAnimalCategoryFilter, setAnimalCategoryFilter } from '@/services/preferencesStorage';

type AnimalCategoryFilterContextValue = {
  filter: AnimalCategoryFilter;
  setFilter: (f: AnimalCategoryFilter) => Promise<void>;
  ready: boolean;
};

const AnimalCategoryFilterContext = createContext<AnimalCategoryFilterContextValue | null>(null);

export function AnimalCategoryFilterProvider({ children }: { children: React.ReactNode }) {
  const [filter, setFilterState] = useState<AnimalCategoryFilter>('ALL');
  const [ready, setReady] = useState(false);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      const f = await getAnimalCategoryFilter();
      if (!cancelled) {
        setFilterState(f);
        setReady(true);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  const setFilter = useCallback(async (f: AnimalCategoryFilter) => {
    setFilterState(f);
    await setAnimalCategoryFilter(f);
  }, []);

  const value = useMemo(
    () => ({ filter, setFilter, ready }),
    [filter, setFilter, ready],
  );

  return (
    <AnimalCategoryFilterContext.Provider value={value}>{children}</AnimalCategoryFilterContext.Provider>
  );
}

export function useAnimalCategoryFilter() {
  const ctx = useContext(AnimalCategoryFilterContext);
  if (!ctx) {
    throw new Error('useAnimalCategoryFilter must be used within AnimalCategoryFilterProvider');
  }
  return ctx;
}
