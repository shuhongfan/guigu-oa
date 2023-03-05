<template>
  <div class="seal" :style="{'--color': color}">
    <div class="circle" :style="{'--color': color}">
      <span class="logo">{{subTitle}}</span>
      <span class="status-text">{{title}}</span>
      <span class="ding">{{desc}}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'seal-avatar',
  props: {
    title: {
      type: String,
      default: '已通过'
    },
    color: {
      type: String,
      default: '#A0E4CD'
    },
    subTitle: {
      type: String,
      default: '硅谷审批'
    },
    desc: {
      type: String,
      default: 'guigui.com'
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
.seal {
  --color: #A0E4CD;
  border: 4px solid var(--color);
  display: inline-block;
  padding: 8px;
  border-radius: 50%;
  transform: rotate(-45deg);
  .circle {
    width: 60px;
    height: 60px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: var(--color);
    font-size: 10px;
    border: 2px solid var(--color);
    border-radius: 50%;
    border-left-color: rgba(0,0,0,0);
    border-right-color: rgba(0,0,0,0);
    .logo {
      transform: scale(0.5);
      margin-bottom: -7px;
    }
    .status-text {
      font-size: 20px;
      font-weight: 700;
      line-height: 24px;
    }
    .ding {
      margin-top: -10px;
      transform: scale(0.7);
    }
  }
}
</style>
