import type { HSL } from './HSL';

export interface MeshStatus {
  meshAddress: number;

  uuid: string;

  status?: boolean;

  brightness?: number;

  temperature?: number;

  hsl?: HSL;

  online?: boolean;
}
