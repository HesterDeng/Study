import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Cesium from 'cesium/Cesium'
import '../node_modules/cesium/Build/Cesium/Widgets/Widgets.css'

Vue.config.productionTip = false

Vue.prototype.Cesium = Cesium;

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
