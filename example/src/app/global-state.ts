import React from 'reactn';
import type { Scene } from '../models/scene';
import { appStorage } from './app-storage';

export class GlobalState {
  public get groups(): Record<number, number[]> {
    return React.getGlobal<GlobalState>().groups;
  }

  public get scenes(): Record<number, Scene> {
    return React.getGlobal<GlobalState>().scenes;
  }

  public async addDeviceToGroup(deviceId: number, groupId: number) {
    let newGroups: Record<number, number[]>;
    if (this.groups[groupId] instanceof Array) {
      newGroups = {
        ...this.groups,
        [groupId]: [
          ...this.groups[groupId].filter((id) => id !== deviceId),
          deviceId,
        ],
      };
    } else {
      newGroups = {
        ...this.groups,
        [groupId]: [deviceId],
      };
    }
    await React.setGlobal<GlobalState>({
      groups: newGroups,
    });
    await appStorage.setGroups(newGroups);
  }

  public async removeDeviceFromGroup(deviceId: number, groupId: number) {
    let newGroups: Record<number, number[]>;
    if (this.groups[groupId] instanceof Array) {
      newGroups = {
        ...this.groups,
        [groupId]: this.groups[groupId].filter((id) => id !== deviceId),
      };
    } else {
      newGroups = {
        ...this.groups,
        [groupId]: [],
      };
    }
    await React.setGlobal<GlobalState>({
      groups: newGroups,
    });
    await appStorage.setGroups(newGroups);
  }

  public async addScene(scene: Scene) {
    const { scenes } = this;
    scenes[scene.id!] = scene;
    await React.setGlobal<GlobalState>({ scenes });
    await appStorage.setScenes(scenes);
  }

  public async removeScene(scene: Scene) {
    const { scenes } = this;
    delete scenes[scene.id!];
    await React.setGlobal<GlobalState>({ scenes });
    await appStorage.setScenes(scenes);
  }

  public async initialize() {
    await React.setGlobal<GlobalState>({
      groups: await appStorage.groups,
      scenes: await appStorage.scenes,
    });
  }
}

export const globalState: GlobalState = new GlobalState();
