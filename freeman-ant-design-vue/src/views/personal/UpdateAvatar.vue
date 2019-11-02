<template>
  <a-modal
    class="update-avatar"
    title="选择头像"
    @cancel="handleCancel"
    :width="710"
    :footer="null"
    v-model="show">
    <a-tabs defaultActiveKey="1" class="avatar-tabs">
      <a-tab-pane tab="后田花子" key="1">
        <template v-for="(avatar, index) in hthz">
          <div class="avatar-wrapper" :key="index">
            <img alt="点击选择" :src="'avatar/' + avatar" @click="change(avatar)">
          </div>
        </template>
      </a-tab-pane>
      <a-tab-pane tab="阿里系" key="2" forceRender>
        <template v-for="(avatar, index) in al">
          <div class="avatar-wrapper" :key="index">
            <img alt="点击选择" :src="'avatar/' + avatar" @click="change(avatar)">
          </div>
        </template>
      </a-tab-pane>
      <a-tab-pane tab="脸萌" key="3">
        <template v-for="(avatar, index) in lm">
          <div class="avatar-wrapper" :key="index">
            <img alt="点击选择" :src="'avatar/' + avatar" @click="change(avatar)">
          </div>
        </template>
      </a-tab-pane>
    </a-tabs>
  </a-modal>
</template>
<script>
const hthz = ['avatar.jpg',
'avatar000.jpeg', 'avatar001.jpeg', 'avatar002.jpeg', 'avatar003.jpg', 'avatar004.jpg', 'avatar005.jpeg', 'avatar006.jpg', 'avatar007.jpg', 'avatar008.jpeg', 'avatar009.jpeg', 'avatar010.jpeg',
'avatar011.jpeg', 'avatar012.jpg', 'avatar013.jpg', 'avatar014.jpg', 'avatar015.jpg', 'avatar016.jpeg']
const al = ['cnrhVkzwxjPwAaCfPbdc.png', 'BiazfanxmamNRoxxVxka.png', 'gaOngJwsRYRaVAuXXcmB.png',
  'WhxKECPNujWoWEFNdnJE.png', 'ubnKSIfAJTxIgXOKlciN.png', 'jZUIxmJycoymBprLOUbT.png']
const lm = ['19034103295190235.jpg', '20180414165920.jpg', '20180414170003.jpg',
  '20180414165927.jpg', '20180414165754.jpg', '20180414165815.jpg',
  '20180414165821.jpg', '20180414165827.jpg', '20180414165834.jpg',
  '20180414165840.jpg', '20180414165846.jpg', '20180414165855.jpg',
  '20180414165909.jpg', '20180414165914.jpg', '20180414165936.jpg',
  '20180414165942.jpg', '20180414165947.jpg', '20180414165955.jpg']
export default {
  props: {
    updateAvatarModelVisible: {
      default: false
    },
    user: {
      required: true
    }
  },
  data () {
    return {
      hthz,
      al,
      lm,
      updating: false
    }
  },
  computed: {
    show: {
      get: function () {
        return this.updateAvatarModelVisible
      },
      set: function () {
      }
    }
  },
  methods: {
    handleCancel () {
      this.$emit('cancel')
    },
    change (avatar) {
      if (this.updating) {
        this.$message.warning('更换头像中，请勿重复点击')
        return
      }
      this.updating = true
      this.$put('user/avatar', {
        username: this.user.username,
        avatar
      }).then(() => {
        this.$emit('success', avatar)
        this.updating = false
      }).catch((r) => {
        console.error(r)
        this.$message.error('更新头像失败')
        this.updating = false
      })
    }
  }
}
</script>
<style lang="less">
  .update-avatar {
    .ant-modal-body {
      padding: 0 1rem 1rem 1rem!important;
      .avatar-tabs {
        .avatar-wrapper {
          display: inline-block;
          img {
            width: 6rem;
            border-radius: 2px;
            display: inline-block;
            margin: .5rem;
            cursor: pointer;
          }
        }
      }
    }
  }
</style>
