export interface MeshStatus {
  meshAddress: number;

  uuid: string;

  status?: boolean;

  brightness?: number;

  temperature?: number;

  hsl?: {
    hue: number;

    saturation: number;

    lightness: number;
  };

  online?: boolean;
}
