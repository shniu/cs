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

# 查看索引节点的使用情况
$ df -i 
$ df -i /dev/sda1

# free 输出的 Cache，是页缓存和可回收 Slab 缓存的和，你可以从 /proc/meminfo ，直接得到它们的大小
$ cat /proc/meminfo | grep -E "SReclaimable|Cached" 
Cached:           748316 kB 
SwapCached:            0 kB 
SReclaimable:     179508 kB 

$ cat /proc/slabinfo | grep -E '^#|dentry|inode' 
# name            <active_objs> <num_objs> <objsize> <objperslab> <pagesperslab> : tunables <limit> <batchcount> <sharedfactor> : slabdata <active_slabs> <num_slabs> <sharedavail> 
xfs_inode              0      0    960   17    4 : tunables    0    0    0 : slabdata      0      0      0 
... 
ext4_inode_cache   32104  34590   1088   15    4 : tunables    0    0    0 : slabdata   2306   2306      0hugetlbfs_inode_cache     13     13    624   13    2 : tunables    0    0    0 : slabdata      1      1      0 
sock_inode_cache    1190   1242    704   23    4 : tunables    0    0    0 : slabdata     54     54      0 
shmem_inode_cache   1622   2139    712   23    4 : tunables    0    0    0 : slabdata     93     93      0 
proc_inode_cache    3560   4080    680   12    2 : tunables    0    0    0 : slabdata    340    340      0 
inode_cache        25172  25818    608   13    2 : tunables    0    0    0 : slabdata   1986   1986      0 
dentry             76050 121296    192   21    1 : tunables    0    0    0 : slabdata   5776   5776      0 


# 找到占用内存最多的缓存类型
# 按下c按照缓存大小排序，按下a按照活跃对象数排序 
$ slabtop 
Active / Total Objects (% used)    : 277970 / 358914 (77.4%) 
Active / Total Slabs (% used)      : 12414 / 12414 (100.0%) 
Active / Total Caches (% used)     : 83 / 135 (61.5%) 
Active / Total Size (% used)       : 57816.88K / 73307.70K (78.9%) 
Minimum / Average / Maximum Object : 0.01K / 0.20K / 22.88K 

  OBJS ACTIVE  USE OBJ SIZE  SLABS OBJ/SLAB CACHE SIZE NAME 
69804  23094   0%    0.19K   3324       21     13296K dentry 
16380  15854   0%    0.59K   1260       13     10080K inode_cache 
58260  55397   0%    0.13K   1942       30      7768K kernfs_node_cache 
   485    413   0%    5.69K     97        5      3104K task_struct 
  1472   1397   0%    2.00K     92       16      2944K kmalloc-2048 
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



![&#x6587;&#x4EF6;&#x7CFB;&#x7EDF;&#x4E0E;&#x78C1;&#x76D8;](../../.gitbook/assets/image%20%2847%29.png)



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



![ext2 / ext3 &#x683C;&#x5F0F;&#x7684;&#x6570;&#x636E;&#x5757;](../../.gitbook/assets/image%20%2843%29.png)

![ext4 &#x683C;&#x5F0F;&#x7684;&#x6570;&#x636E;&#x5757;](../../.gitbook/assets/image%20%2854%29.png)

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



![](../../.gitbook/assets/image%20%2841%29.png)

首先，块组描述符表不会保存所有块组的描述符了，而是将块组分成多个组，我们称为元块组（Meta Block Group）。每个元块组里面的块组描述符表仅仅包括自己的，一个元块组包含 64 个块组，这样一个元块组中的块组描述符表最多 64 项。我们假设一共有 256 个块组，原来是一个整的块组描述符表，里面有 256 项，要备份就全备份，现在分成 4 个元块组，每个元块组里面的块组描述符表就只有 64 项了，这就小多了，而且四个元块组自己备份自己的。



目录存储

![&#x76EE;&#x5F55;&#x683C;&#x5F0F;](../../.gitbook/assets/image%20%2844%29.png)

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



![&#x8F6F;&#x8FDE;&#x63A5;&#x548C;&#x786C;&#x8FDE;&#x63A5;](../../.gitbook/assets/image%20%2845%29.png)



![inode &#x548C; &#x6570;&#x636E;&#x5757;](../../.gitbook/assets/image%20%2852%29.png)



### 虚拟文件系统 （VFS）

VFS 定义了一组所有文件系统都支持的数据结构和标准接口。这样，用户进程和内核中的其他子系统，就只需要跟 VFS 提供的统一接口进行交互。为了降低慢速磁盘对性能的影响，文件系统又通过页缓存、目录项缓存以及索引节点缓存，缓和磁盘延迟对应用程序的影响。

![](../../.gitbook/assets/image%20%2849%29.png)

文件

要打开一个文件，首先要通过 get\_unused\_fd\_flags 得到一个没有用的文件描述符；在每一个进程的 task\_struct 中，有一个指针 files，类型是 files\_struct, files\_struct 里面最重要的是一个文件描述符列表，每打开一个文件，就会在这个列表中分配一项，下标就是文件描述符，**对于任何一个进程，默认情况下，文件描述符 0 表示 stdin 标准输入，文件描述符 1 表示 stdout 标准输出，文件描述符 2 表示 stderr 标准错误输出。另外，再打开的文件，都会从这个列表中找一个空闲位置分配给它**。

文件描述符列表的每一项都是一个指向 struct file 的指针，也就是说，每打开一个文件，都会有一个 struct file 对应。（内核会先创建一个fd，然后调用 do\_filp\_open 创建一个 struct file，然后将 fd 和 file 进行关联绑定）

```c
// ...
struct files_struct    *files;

// ...

struct files_struct {
......
  struct file __rcu * fd_array[NR_OPEN_DEFAULT];
};
```



![VFS](../../.gitbook/assets/image%20%2846%29.png)

对于每一个进程，打开的文件都有一个文件描述符，在 files\_struct 里面会有文件描述符数组。每个一个文件描述符是这个数组的下标，里面的内容指向一个 file 结构，表示打开的文件。这个结构里面有这个文件对应的 inode，最重要的是这个文件对应的操作 file\_operation。如果操作这个文件，就看这个 file\_operation 里面的定义了。

对于每一个打开的文件，都有一个 dentry 对应，虽然叫作 directory entry，但是不仅仅表示文件夹，也表示文件。它最重要的作用就是指向这个文件对应的 inode。如果说 file 结构是一个文件打开以后才创建的，dentry 是放在一个 dentry cache 里面的，文件关闭了，他依然存在，因而他可以更长期地维护内存中的文件的表示和硬盘上文件的表示之间的关系。inode 结构就表示硬盘上的 inode，包括块设备号等。

几乎每一种结构都有自己对应的 operation 结构，里面都是一些方法，因而当后面遇到对于某种结构进行处理的时候，如果不容易找到相应的处理函数，就先找这个 operation 结构，就清楚了。

#### 索引节点和目录项的抽象理解

索引节点和目录项纪录了文件的元数据，以及文件间的目录关系；索引节点是每个文件的唯一标志，而目录项维护的正是文件系统的树状结构。目录项和索引节点的关系是多对一，你可以简单理解为，一个文件可以有多个别名

![&#x76EE;&#x5F55;&#x9879;&#x548C;&#x7D22;&#x5F15;&#x8282;&#x70B9;&#x7684;&#x5173;&#x7CFB;](../../.gitbook/assets/image%20%2839%29.png)

1. 目录项本身就是一个内存缓存，而索引节点则是存储在磁盘中的数据。在前面的 Buffer 和 Cache 原理中，我曾经提到过，为了协调慢速磁盘与快速 CPU 的性能差异，文件内容会缓存到页缓存 Cache 中。
2. 磁盘在执行文件系统格式化时，会被分成三个存储区域，超级块、索引节点区和数据块区





### 资料参考

* [理解 Inode](https://www.ruanyifeng.com/blog/2011/12/inode.html)
* [Ext4 文件系统架构分析1](https://www.cnblogs.com/alantu2018/p/8461272.html)
* [Ext4 文件系统架构分析2](https://www.cnblogs.com/wuchanming/p/3737758.html)
* [Ext4 文件系统架构分析3](https://www.cnblogs.com/alantu2018/p/8461598.html)

