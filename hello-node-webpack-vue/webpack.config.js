var path = require('path');
var webpack = require('webpack');
const VueLoaderPlugin = require('vue-loader/lib/plugin');
const ExtractTextPlugin = require("extract-text-webpack-plugin");//css独立打包
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
var HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
let cesiumSource = './node_modules/cesium/Source';
let cesiumWorkers = '../Build/Cesium/Workers';
module.exports = {
    mode: 'development',
    devtool: 'source-map',
    devtool: false,
    entry: {
        "app": './src/source/app.js'
    },
    output: {
        filename: "[name].js",      //打包后输出文件的文件名
        chunkFilename: '[name].js',    // 分离出来js模块的名字
        path: path.resolve(__dirname, './src/dist/'),
        sourcePrefix: ''
    },
    amd: {
        toUrlUndefined: true    // Enable webpack-friendly use of require in Cesium
    },
    node: {
        fs: 'empty'  // Resolve node module use of fs
    },
    watch: true,
    watchOptions: {
        poll: 1000,
        ignored: /node_modules/
    },
    devServer: {
        port: 8088,
        hot: true,
        open: 'chrome',
        inline: true,
        progress: true, //显示打包的进度
        contentBase: __dirname + '/src', // 默认值就是项目根目录
        historyApiFallback: true,
        overlay: true
    },
    resolve: {
        extensions: ['.js', '.vue', '.json'],
        alias: {
            'vue$': 'vue/dist/vue.esm.js',
            'cesium': path.resolve(__dirname, cesiumSource),
            'lodash': "lodash"
        }
    },
    plugins: [
        new VueLoaderPlugin(),
        new HtmlWebpackPlugin({
            title: '智隧数字化平台',
            template: './src/source/template.ejs',
        }),
        new webpack.ProvidePlugin({
            'cesium': "cesium",
            "_": "lodash"
        }),
        new MiniCssExtractPlugin({  //css文件打包生成main[hash].css文件在dist目录
            filename: '[name][hash].css',//[name]=output.filename
            chunkFilename: '[id].css',
            ignoreOrder: false
        }),
        new CopyWebpackPlugin([{from: path.join(cesiumSource, cesiumWorkers), to: 'Workers'}]),
        new CopyWebpackPlugin([{from: path.join(cesiumSource, 'Assets'), to: 'Assets'}]),
        new CopyWebpackPlugin([{from: path.join(cesiumSource, 'Widgets'), to: 'Widgets'}]),
        new CopyWebpackPlugin([{from: path.join(cesiumSource, 'ThirdParty/Workers'), to: 'ThirdParty/Workers'}]),
        new webpack.DefinePlugin({
            CESIUM_BASE_URL: JSON.stringify('./')
        })
    ],
    optimization: {
        minimize: false,//默认压缩
        removeEmptyChunks: true,
        removeAvailableModules: true,
        mergeDuplicateChunks: true,
        splitChunks: {  //打包分割出来的bundle,splitChunks默认有两个缓存组：vender和default
            chunks: "all",
            minSize: 30000,  //表示在压缩前的最小模块大小,默认值是30kb
            minChunks: 1,  // 表示被引用次数，默认为1；
            maxAsyncRequests: 5,  //所有异步请求不得超过5个
            maxInitialRequests: 3,  //初始话并行请求不得超过3个
            automaticNameDelimiter: '~',// 打包分隔符
            cacheGroups: {
                vendors: {
                    name: "vendor",
                    minSize: 10240,
                    maxSize: 102400,
                    test: /[\\/]node_modules[\\/]/,
                    priority: 3
                },
                timjs: {
                    name: "timjs",//
                    test: /[\\/]src[\\/]source[\\/]scripts[\\/]/,
                    chunks: "all",
                    minChunks: 1,//最小公用模块的次数
                    priority: 2
                }
            }
        }
    },
    module: {
        unknownContextCritical: /^.\/.*$/,
        unknownContextCritical: false,
        rules: [
            {
                test: /\.css$/,
                use: [
                    'vue-style-loader',//针对vue文件中的css代码
                    'css-loader'
                ]
            },
            // {
            //     test: /\.css$/,//缺点不支持hot deploy使用
            //     use: [
            //         {
            //             loader: MiniCssExtractPlugin.loader
            //         },
            //         'css-loader',
            //     ]
            // },
            {test: /\.(ttf|eot|woff|woff2)$/, use: 'url-loader'},
            {
                test: /\.(gltf|glb)$/,
                loader: 'file-loader',
                options: {
                    name: 'model/[name].[ext]'
                }
            },
            {
                test: /\.svg/,
                use: ['file-loader']
            },
            {
                test: /\.scss$/,
                use: [
                    'vue-style-loader',
                    'css-loader',
                    'sass-loader'
                ],
            },
            {
                test: /\.less$/,
                use: [
                    'style-loader',
                    'css-loader',
                    'less-loader'
                ]
            },
            {
                test: /\.sass$/,
                use: [
                    'vue-style-loader',
                    'css-loader',
                    'sass-loader?indentedSyntax'
                ],
            },
            {
                test: /\.(png|jpg|gif|svg)$/, //支持图片的打包
                use: [{
                    loader: 'file-loader',
                    options: {
                        name: '[path][name].[ext]?[hash]'
                    }
                }]
            },
            {test: /\.ejs$/, use: ['ejs-loader']},
            {
                test: /\.js$/,  //支持es6
                loader: 'babel-loader',
                query: {compact: false},
                exclude: [
                    /node_modules/,
                    /Scripts/,
                    /assets/
                ]
            },
            {
                test: /\.vue$/,
                loader: 'vue-loader',
                options: {
                    loaders: {
                        'scss': [
                            'vue-style-loader',
                            'css-loader',
                            'sass-loader'
                        ],
                        'sass': [
                            'vue-style-loader',
                            'css-loader',
                            'sass-loader?indentedSyntax'
                        ]
                    }
                }
            }
        ]
    }
};
