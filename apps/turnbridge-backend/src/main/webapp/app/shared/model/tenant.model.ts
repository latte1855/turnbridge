export interface ITenant {
  id?: number;
  name?: string;
  code?: string;
  status?: string | null;
}

export const defaultValue: Readonly<ITenant> = {};
