/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View, Button, NativeModules} from 'react-native';

const instructions = Platform.select({
    ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
    android:
        'Double tap R on your keyboard to reload,\n' +
        'Shake or press menu button for dev menu',
});

type Props = {};
export default class App extends Component<Props> {
    render() {
        return (
            <View style={styles.container}>
                <Text style={styles.welcome}>Welcome to React Native!</Text>
                <Text style={styles.instructions}>To get started, edit App.js</Text>
                <Text style={styles.instructions}>{instructions}</Text>

                <Button
                    title={"点击"}
                    onPress={() => {
                        NativeModules.LiveShow.DownloadList("").then(() => {

                        });
                    }}/>

                <Button
                    title={"点击直播回放"}
                    onPress={() => {
                        NativeModules.LiveShow.LiveShow({
                            accessToken: "yEzMxMzM2UWMyEWYwM2YxAjZkJTOjJWOxYWN4QjYkZDf8xXfiQzMzQjMy8FN0MTM2UjI6ISZtFmbyJCLwojIhJCLiAjI6ICZpdmIs01W6Iic0RXYiwCOzkDM0MTM1UTM6ISZtlGdnVmciwiI0ITNyMTO1UjI6ICZphnIsUzMzETM6ICZpBnIsISVTx0bwlmYs10QQZDOqBlI6IyclR2bjJCL0MzM0IjM6ICZp9VZzJXdvNmIsIiI6IichRXY2FmIsAjOiIXZk5WZnJCL4MzNxUzMxUTNxojIlJXawhXZiwCN0MTM2UjOiQWat92byJCLiATNiVTdcBTNiVTdchjM3YTdcJiOiUWbh52ajlmbiwiIyV2c1JiOiUGbvJnIsICN1AzNyETMiojIklWdiwSNzMTMxojIkl2XyVmb0JXYwJye",
                            title: "这是一个标题",
                            playbackId:1495,
                            type: "nolive" //live
                        });
                    }}/>


                <Button
                    title={"点击直播1"}
                    onPress={() => {
                        NativeModules.LiveShow.LiveShow({
                            accessToken: "MDZjZjN0UmZxkjZiVWMwIWYzQDO1UGO1kTYwIzN3YjZ8xHf9JSM2kzNxIzX5kjM2gzNiojIl1WYuJnIsAjOiEmIs01W6Iic0RXYiwyMwYDO1MTM1UTM6ISZtlGdnVmciwiI3ETN1UDN3UjI6ICZphnIsUzMzETM6ICZpBnIsAjOiQWanJCLxYTO3EjM6ICZp9VZzJXdvNmIsIiI6IichRXY2FmIsAjOiIXZk5WZnJCLzADN5YzMxUTNxojIlJXawhXZiwSO5IjN4cjOiQWat92byJCLiATNiVTdcBTNiVTdchjM3YTdcJiOiUWbh52ajlmbiwiIyV2c1JiOiUGbvJnIsICN1AzNyETMiojIklWdiwSNzMTMxojIkl2XyVmb0JXYwJye",
                            title: "这是一个标题",
                            type: "live" //live
                        });
                    }}/>

                <Button
                    title={"点击直播1"}
                    onPress={() => {
                        NativeModules.LiveShow.LiveShow({
                            accessToken: "EjN2ImNkVTZ4kDN4kjN5IjZhVDO2UmYiN2Y5cDOxQGM8xHf9JSM3MjNzIzX4MzMxYTNiojIl1WYuJnIsAjOiEmIs01W6Iic0RXYiwSN3ETO1MTM1UTM6ISZtlGdnVmciwiI4UDM1cDOwYjI6ICZphnIsUzMzETM6ICZpBnIsAjOiQWanJCLxczM2MjM6ICZp9VZzJXdvNmIsIiI6IichRXY2FmIsAjOiIXZk5WZnJCL1cTO5YzMxUTNxojIlJXawhXZiwCOzMTM2UjOiQWat92byJCLiATNiVTdcBTNiVTdchjM3YTdcJiOiUWbh52ajlmbiwiIyV2c1JiOiUGbvJnIsICN1AzNyETMiojIklWdiwSNzMTMxojIkl2XyVmb0JXYwJye",
                            title: "这是一个标题",
                            type: "live" //live
                        });
                    }}/>

                <Button
                    title={"本地的activity点击"}
                    onPress={() => {
                        NativeModules.DialogModule.showDialog("");
                    }}/>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    welcome: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
    instructions: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
});
