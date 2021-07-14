import AsyncStorage from '@react-native-async-storage/async-storage';
import nameof from 'ts-nameof.macro';
import type { Scene } from '../models/scene';

export class AppStorage {
  public get groups(): Promise<Record<number, number[]>> {
    return AsyncStorage.getItem(nameof(this.groups)).then(
      (data: string | null) => {
        if (data) {
          return JSON.parse(data) || {};
        }
        return {};
      }
    );
  }

  public get scenes(): Promise<Record<number, Scene>> {
    return AsyncStorage.getItem(nameof(this.scenes)).then(
      (data: string | null) => {
        if (data) {
          return JSON.parse(data) || {};
        }
        return {};
      }
    );
  }

  public async setGroups(groups: Record<number, number[]>) {
    await AsyncStorage.setItem(nameof(this.groups), JSON.stringify(groups));
  }

  public async setScenes(scenes: Record<number, Scene>) {
    await AsyncStorage.setItem(nameof(this.scenes), JSON.stringify(scenes));
  }
}

export const appStorage = new AppStorage();
