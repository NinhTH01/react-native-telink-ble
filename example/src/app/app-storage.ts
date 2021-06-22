import AsyncStorage from '@react-native-async-storage/async-storage';
import nameof from 'ts-nameof.macro';

export class AppStorage {
  public networkKey?: string;

  public async setNetworkKey(networkKey: string) {
    this.networkKey = networkKey;
    await AsyncStorage.setItem(nameof(this.networkKey), networkKey);
  }

  public async initialize() {
    this.networkKey =
      (await AsyncStorage.getItem(nameof(this.networkKey))) ?? '';
  }
}

export const appStorage = new AppStorage();
