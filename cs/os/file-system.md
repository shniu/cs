# 文件系统

### 文件系统功能规划

1. 文件系统要有严格的组织形式，使得文件能够以块为单位进行存储
2. 文件系统中也要有索引区，用来方便查找一个文件分成的多个块都存放在了什么位置
3. 如果文件系统中有的文件是热点文件，近期经常被读取和写入，文件系统应该有缓存层
4. 文件应该用文件夹的形式组织起来，方便管理和查询
5. Linux 内核要在自己的内存里面维护一套数据结构，来保存哪些文件被哪些进程打开和使用
6. 在用户态，每个打开的文件都有一个文件描述符，可以通过各种文件相关的系统调用，操作这个文件描述符，文件描述符属于特定的进程

### 文件系统相关命令行

```bash
# 查看格式化和没有格式化的分区
$ fdisk -l

# 格式化磁盘
$ mkfs.ext4 /dev/vdc

# 选择将磁盘格式化为多个分区
$ fdisk /dev/vdc

# 挂载
$ mount /dev/vdc /var/log

# 卸载
$ unmount /var/log

# 查看文件
$ ls -l
# - 表示普通文件；
# d 表示文件夹；
# c 表示字符设备文件，
# b 表示块设备文件
# s 表示套接字 socket 文件，
# l 表示符号链接，也即软链接，就是通过名字指向另外一个文件

# 查看文件信息, 如 inode
$ stat filename
$ ls -i filename
$ df -i
```

### 系统调用

文件描述符：是用来区分一个进程打开的多个文件的。它的作用域就是当前进程，出了当前进程这个文件描述符就没有意义了。open 返回的 fd 必须记录好，我们对这个文件的所有操作都要靠这个 fd，包括最后关闭文件。在调用 open\(\) 系统调用时，OS 会返回一个 fd

```c

int stat(const char *pathname, struct stat *statbuf);
int fstat(int fd, struct stat *statbuf);
int lstat(const char *pathname, struct stat *statbuf);


struct stat {
  dev_t     st_dev;         /* ID of device containing file */
  ino_t     st_ino;         /* Inode number */
  mode_t    st_mode;        /* File type and mode */
  nlink_t   st_nlink;       /* Number of hard links */
  uid_t     st_uid;         /* User ID of owner */
  gid_t     st_gid;         /* Group ID of owner */
  dev_t     st_rdev;        /* Device ID (if special file) */
  off_t     st_size;        /* Total size, in bytes */
  blksize_t st_blksize;     /* Block size for filesystem I/O */
  blkcnt_t  st_blocks;      /* Number of 512B blocks allocated */
  struct timespec st_atim;  /* Time of last access */
  struct timespec st_mtim;  /* Time of last modification */
  struct timespec st_ctim;  /* Time of last status change */
};
```



![&#x6587;&#x4EF6;&#x7CFB;&#x7EDF;&#x4E0E;&#x78C1;&#x76D8;](../../.gitbook/assets/image%20%2845%29.png)



### inode 与块的存储

磁盘被格式化的目的是：文件系统如何更好的管理文件，包括存储和读取等。一个磁盘有很大的空间，磁盘由很多个扇区组成，连续的扇区又组成一个块，文件系统使用的基本单位是块。

```c
// inode 结构
struct ext4_inode {
  __le16  i_mode;    /* File mode */
  __le16  i_uid;    /* Low 16 bits of Owner Uid */
  __le32  i_size_lo;  /* Size in bytes */
  __le32  i_atime;  /* Access time */
  __le32  i_ctime;  /* Inode Change time */
  __le32  i_mtime;  /* Modification time */
  __le32  i_dtime;  /* Deletion Time */
  __le16  i_gid;    /* Low 16 bits of Group Id */
  __le16  i_links_count;  /* Links count */
  __le32  i_blocks_lo;  /* Blocks count */
  __le32  i_flags;  /* File flags */
......
  __le32  i_block[EXT4_N_BLOCKS];/* Pointers to blocks */
  __le32  i_generation;  /* File version (for NFS) */
  __le32  i_file_acl_lo;  /* File ACL */
  __le32  i_size_high;
......
};

// EXT4_N_BLOCKS = 15
#define  EXT4_NDIR_BLOCKS    12
#define  EXT4_IND_BLOCK      EXT4_NDIR_BLOCKS
#define  EXT4_DIND_BLOCK      (EXT4_IND_BLOCK + 1)
#define  EXT4_TIND_BLOCK      (EXT4_DIND_BLOCK + 1)
#define  EXT4_N_BLOCKS      (EXT4_TIND_BLOCK + 1)
```



![ext2 / ext3 &#x683C;&#x5F0F;&#x7684;&#x6570;&#x636E;&#x5757;](../../.gitbook/assets/image%20%2842%29.png)

![ext4 &#x683C;&#x5F0F;&#x7684;&#x6570;&#x636E;&#x5757;](../../.gitbook/assets/image%20%2852%29.png)

ext4 引入了块组，一个块组包含一系列连续的块，以4KB的数据块为例，一个块组可以包含32768个数据块，也就是128MB, 这也是说 ext4 中的一个 extent 最大可以表示 128MB

一个 inode 大小一般为 256 Byte，一个文件对应一个 inode

```c
// extent header
struct ext4_extent_header {
  __le16  eh_magic;  /* probably will support different formats */
  __le16  eh_entries;  /* number of valid entries */
  __le16  eh_max;    /* capacity of store in entries */
  __le16  eh_depth;  /* has tree real underlying blocks? */
  __le32  eh_generation;  /* generation of the tree */
};

/*
 * This is the extent on-disk structure.
 * It's used at the bottom of the tree.
 */
struct ext4_extent {
  __le32  ee_block;  /* first logical block extent covers */
  __le16  ee_len;    /* number of blocks covered by extent */
  __le16  ee_start_hi;  /* high 16 bits of physical block */
  __le32  ee_start_lo;  /* low 32 bits of physical block */
};
/*
 * This is index on-disk structure.
 * It's used at all the levels except the bottom.
 */
struct ext4_extent_idx {
  __le32  ei_block;  /* index covers logical blocks from 'block' */
  __le32  ei_leaf_lo;  /* pointer to the physical block of the next *
         * level. leaf or next index could be there */
  __le16  ei_leaf_hi;  /* high 16 bits of physical block */
  __u16  ei_unused;
};
```



![](../../.gitbook/assets/image%20%2839%29.png)

首先，块组描述符表不会保存所有块组的描述符了，而是将块组分成多个组，我们称为元块组（Meta Block Group）。每个元块组里面的块组描述符表仅仅包括自己的，一个元块组包含 64 个块组，这样一个元块组中的块组描述符表最多 64 项。我们假设一共有 256 个块组，原来是一个整的块组描述符表，里面有 256 项，要备份就全备份，现在分成 4 个元块组，每个元块组里面的块组描述符表就只有 64 项了，这就小多了，而且四个元块组自己备份自己的。



目录存储

![&#x76EE;&#x5F55;&#x683C;&#x5F0F;](../../.gitbook/assets/image%20%2843%29.png)

```c

struct ext4_dir_entry {
  __le32  inode;      /* Inode number */
  __le16  rec_len;    /* Directory entry length */
  __le16  name_len;    /* Name length */
  char  name[EXT4_NAME_LEN];  /* File name */
};
struct ext4_dir_entry_2 {
  __le32  inode;      /* Inode number */
  __le16  rec_len;    /* Directory entry length */
  __u8  name_len;    /* Name length */
  __u8  file_type;
  char  name[EXT4_NAME_LEN];  /* File name */
};
```



![&#x8F6F;&#x8FDE;&#x63A5;&#x548C;&#x786C;&#x8FDE;&#x63A5;](../../.gitbook/assets/image%20%2844%29.png)



![inode &#x548C; &#x6570;&#x636E;&#x5757;](../../.gitbook/assets/image%20%2850%29.png)



### 虚拟文件系统 （VFS）

VFS 定义了一组所有文件系统都支持的数据结构和标准接口。这样，用户进程和内核中的其他子系统，就只需要跟 VFS 提供的统一接口进行交互。为了降低慢速磁盘对性能的影响，文件系统又通过页缓存、目录项缓存以及索引节点缓存，缓和磁盘延迟对应用程序的影响。

![](../../.gitbook/assets/image%20%2847%29.png)

### 资料参考

* [理解 Inode](https://www.ruanyifeng.com/blog/2011/12/inode.html)
* [Ext4 文件系统架构分析1](https://www.cnblogs.com/alantu2018/p/8461272.html)
* [Ext4 文件系统架构分析2](https://www.cnblogs.com/wuchanming/p/3737758.html)
* [Ext4 文件系统架构分析3](https://www.cnblogs.com/alantu2018/p/8461598.html)

