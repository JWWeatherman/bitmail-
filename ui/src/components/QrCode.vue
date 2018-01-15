<template>
  <div id="qr-code">
    <canvas id="qr"></canvas>
  </div>
</template>
<script>
  import QRious from 'qrious'
  export default {
    name: 'qr-code',
    mounted () {
      const hash = this.hash
      this.qr = new QRious({
        element: document.getElementById('qr'),
        backgroundAlpha: 0.8,
        foregroundAlpha: 0.8,
        level: 'H',
        padding: 25,
        size: 250,
        value: hash
      })
    },
    data () {
      return {
        qr: null
      }
    },
    props: ['hash']
  }
</script>
