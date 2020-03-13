<template>
    <div id="cesiumContainer"></div>
</template>

<script>
  export default {
    name: 'cesiumViewer',
    mounted(){
      var viewer = new this.Cesium.Viewwe("cesiumContainer");
    }
  }
</script>

<style scoped>

</style>
