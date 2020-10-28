# 容器技术

### 容器发展史

via: [容器技术之发展简史](https://mp.weixin.qq.com/s/ccFkJJz97KcuXdO3r5zdXA)

![](../../.gitbook/assets/image%20%2871%29.png)

容器技术需要解决的核心问题之一运行时的环境隔离，目标是给容器构造一个无差别的运行时环境，用以在任意时间、任意位置运行容器镜像。本源：**容器需要运行时隔离技术来保证容器的运行环境符合预期**。习惯上，大家把这种实现容器隔离技术的组件叫做容器运行时。

从另外一个角度看，**容器隔离技术解决的是资源供给问题**。对于软件运行环境的隔离要求，从操作系统出现之初就有了。多任务分时操作系统和进程虚拟地址都是为了解决多个任务运行在同一台主机上的资源共享问题，让每个进程都以为自己独占主机。当然仅仅是进程隔离是远远不够的。

![](../../.gitbook/assets/image%20%2876%29.png)

LXC \(Linux Container\) 是目前 Linux 提供的完整的容器技术实现基础，包括了 Namespace 和 Cgroups 等

Docker真正核心的创新是容器镜像（docker image），一种新型的应用打包、分发和运行机制。容器镜像将应用**运行环境**，包括代码、依赖库、工具、资源文件和元信息等，打包成一种**操作系统发行版无关**的**不可变更**软件包。

* 容器镜像打包了整个容器运行依赖的环境，以避免依赖运行容器的服务器的操作系统，从而实现“build once，run anywhere”。
* 容器镜像一但构建完成，就变成read only，成为不可变基础设施的一份子。
* 操作系统发行版无关，核心解决的是容器进程对操作系统包含的库、工具、配置的依赖，但是容器镜像无法解决容器进程对内核特性的特殊依赖。

Docker 的出现解决了最核心的两个问题：**如何发布软件**和**如何运行软件。**Docker作为一个单机软件打包、发布、运行系统，其价值是非常巨大的，它引领了容器技术的腾飞。

虽然 Docker 为我们带来了很大的变革，但容器编排才是未来。Google 的 LXC + Borg 可以说是最早的容器编排框架。站在 Borg 的肩膀上，Google 联合个大云厂商推出了 Kubenetes, 主要用于解决大规模集群的容器部署、运行、管理等问题。Kubernetes 在容器的基础上增加了一层的新的管理抽象 Pod，以便更好地利用容器进行应用的功能模块切分。得益于 Google 在大规模集群基础设施建设的强大积累，脱胎于 Borg 的 K8S 很快成为了行业的标准应用，堪称容器编排的必备工具。

![](../../.gitbook/assets/image%20%2875%29.png)

### 安全容器

**Kata Containers，Firecracker， Google** gVisor 都是安全容器的未来。AWS已经证明了安全容器是公有云落地Serverless的关键技术之一，与之类似，**边缘计算**也将成为安全容器的典型应用场景。

由于操作系统内核漏洞，Docker组件设计缺陷，以及不当的配置都会导致Docker容器发生逃逸，从而获取宿主机权限。由于频发的安全及逃逸漏洞，在公有云环境容器应用不得不也运行在虚拟机中，从而满足多租户安全隔离要求。而分配、管理、运维这些传统虚拟机与容器轻量、灵活、弹性的初衷背道而驰，同时在资源利用率、运行效率上也存浪费。

这就是云原生里面的多租户问题，其本质是容器安全问题。

安全问题的唯一正解在于允许那些（导致安全问题的）Bug发生，但通过额外的隔离层来阻挡住它们。

—— LinuxCon NA 2015, Linus Torvalds

可以持续关注 [https://katacontainers.io/](https://katacontainers.io/)， Kata Containers is an open source container runtime, building lightweight virtual machines that seamlessly plug into the containers ecosystem.

### rootfs \(Root File System\)

what is rootfs? \(什么是 rootfs ?\)

![rootfs](../../.gitbook/assets/image%20%2874%29.png)

Rootfs is a special instance of ramfs \(or tmpfs, if that's enabled\), which is always present in 2.6 systems. You can't unmount rootfs for approximately the same reason you can't kill the init process; rather than having special code to check for and handle an empty list, it's smaller and simpler for the kernel to just make sure certain lists can't become empty. \(Rootfs是ramfs（或者tmpfs，如果启用的话）的一个特殊实例，它在2.6系统中一直存在。你不能卸载rootfs的原因和你不能杀死init进程的原因大致相同；比起有特殊的代码来检查和处理空列表，内核只需要确保某些列表不能变空就可以了，这更小更简单。\)

Most systems just mount another filesystem over rootfs and ignore it. The amount of space an empty instance of ramfs takes up is tiny. \(大多数系统只是在rootfs上挂载另一个文件系统，而忽略它。一个空的ramfs实例所占用的空间是很小的。\)

If CONFIG\_TMPFS is enabled, rootfs will use tmpfs instead of ramfs by default. To force ramfs, add "rootfstype=ramfs" to the kernel command line. \(如果启用了 CONFIG\_TMPFS，rootfs 默认会使用 tmpfs 而不是 ramfs。要强制使用ramfs，在内核命令行添加 "rootfstype=ramfs"。\)

It contains all applications, settings, devices, data and more. Without the root file system, your Linux system can not run. \(它包含了所有的应用程序、设置、设备、数据等。没有根文件系统，你的Linux系统就无法运行。\)

what is ramfs? \(什么是 ramfs?\)

Ramfs is a very simple filesystem that exports Linux's disk caching mechanisms \(the page cache and dentry cache\) as a dynamically resizable RAM-based filesystem. \(Ramfs 是一个非常简单的文件系统，它将 Linux 的磁盘缓存机制输出为基于 RAM 的动态可调整大小的文件系统。\)

Normally all files are cached in memory by Linux. Pages of data read from backing store \(usually the block device the filesystem is mounted on\) are kept around in case it's needed again, but marked as clean \(freeable\) in case the Virtual Memory system needs the memory for something else. Similarly, data written to files is marked clean as soon as it has been written to backing store, but kept around for caching purposes until the VM reallocates the memory. A similar mechanism \(the dentry cache\) greatly speeds up access to directories. \(通常情况下所有的文件都被 Linux 缓存在内存中。从备份存储（通常是文件系统挂载的块设备）中读取的数据页被保留在周围，以备再次需要，但在虚拟内存系统需要内存做其他事情的情况下被标记为干净（可释放）。同样，写入文件的数据一旦被写入备份存储，就会被标记为干净，但为了缓存目的而保留在周围，直到虚拟内存重新分配内存。类似的机制（**dentry cache**）大大加快了对目录的访问速度。\)

With ramfs, there is no backing store. Files written into ramfs allocate dentries and page cache as usual, but there's nowhere to write them to. This means the pages are never marked clean, so they can't be freed by the VM when it's looking to recycle memory.\(在ramfs中，没有备份存储。写入ramfs的文件会像往常一样分配dentry和页面缓存，但没有地方写它们。这意味着页面永远不会被标记为干净，所以当虚拟机要回收内存时，它们不能被虚拟机释放。 \)

The amount of code required to implement ramfs is tiny, because all the work is done by the existing Linux caching infrastructure. Basically, you're mounting the disk cache as a filesystem. Because of this, ramfs is not an optional component removable via menuconfig, since there would be negligible space savings. \(实现ramfs所需的代码量很小，因为所有的工作都是由现有的Linux缓存基础设施完成的。基本上，你是把磁盘缓存作为一个文件系统来挂载的。正因为如此，ramfs并不是一个可以通过menuconfig移除的可选组件，因为这样可以节省很多空间。\)

via: [https://www.kernel.org/doc/Documentation/filesystems/ramfs-rootfs-initramfs.txt](https://www.kernel.org/doc/Documentation/filesystems/ramfs-rootfs-initramfs.txt)

-- 

The root filesystem should generally be small, since it contains very critical files and a small, infrequently modified filesystem has a better chance of not getting corrupted. A corrupted root filesystem will generally mean that the system becomes unbootable except with special measures \(e.g., from a floppy\), so you don't want to risk it. \(根文件系统一般应该很小，因为它包含了非常关键的文件，而且一个小的、不经常修改的文件系统有较大的机会不被破坏。损坏的根文件系统一般意味着系统变得无法启动，除非采取特殊措施（如从软盘启动），所以你不想冒这个险。\)

The root directory generally doesn't contain any files, except perhaps on older systems where the standard boot image for the system, usually called /vmlinuz was kept there. \(Most distributions have moved those files the the /boot directory. Otherwise, all files are kept in subdirectories under the root filesystem: \(根目录一般不包含任何文件，除非是在旧系统中，系统的标准启动映像（通常称为/vmlinuz）被保存在那里。大多数发行版已经将这些文件移到了/boot目录下。否则，所有文件都保存在根目录下的子目录中。\)

```text
/bin
Commands needed during bootup that might be used by normal users (probably after bootup).
(开机时需要的命令，普通用户可能会用到（可能是开机后）。)

/sbin
Like /bin, but the commands are not intended for normal users, although they may use them if necessary and allowed. /sbin is not usually in the default path of normal users, but will be in root's default path.
(和/bin一样，但这些命令并不是给普通用户使用的，尽管他们可能会在必要和允许的情况下使用它们。/sbin通常不在普通用户的默认路径中，但会在root的默认路径中。)

/etc
Configuration files specific to the machine.
(机器特有的配置文件。)

/root
The home directory for user root. This is usually not accessible to other users on the system
(用户root的主目录。系统中的其他用户通常无法访问该目录。)

/lib
Shared libraries needed by the programs on the root filesystem.
(根文件系统中的程序需要的共享库。)

/lib/modules
Loadable kernel modules, especially those that are needed to boot the system when recovering from disasters (e.g., network and filesystem drivers).
(可加载的内核模块，特别是那些从灾难中恢复时启动系统所需的模块（例如，网络和文件系统驱动程序）。)

/dev
Device files. These are special files that help the user interface with the various devices on the system.
(设备文件。这些都是帮助用户与系统上各种设备对接的特殊文件。)

/tmp
Temporary files. As the name suggests, programs running often store temporary files in here.

/boot
Files used by the bootstrap loader, e.g., LILO or GRUB. Kernel images are often kept here instead of in the root directory. If there are many kernel images, the directory can easily grow rather big, and it might be better to keep it in a separate filesystem. Another reason would be to make sure the kernel images are within the first 1024 cylinders of an IDE disk. This 1024 cylinder limit is no longer true in most cases. With modern BIOSes and later versions of LILO (the LInux LOader) the 1024 cylinder limit can be passed with logical block addressing (LBA). See the lilo manual page for more details.
(Bootstrap Loader 使用的文件，例如 LILO 或 GRUB。内核镜像通常保存在这里而不是根目录。如果有很多内核映像，这个目录很容易变大，最好把它放在一个单独的文件系统中。另一个原因是确保内核映像在IDE磁盘的前1024个圆柱体内。在大多数情况下，1024缸的限制已经不再适用了。在现代BIOS和LILO(LInux LOader)的更新版本中，1024个圆柱体的限制可以通过逻辑块寻址(LBA)来实现。更多细节请参见lilo手册。)

/mnt
Mount point for temporary mounts by the system administrator. Programs aren't supposed to mount on /mnt automatically. /mnt might be divided into subdirectories (e.g., /mnt/dosa might be the floppy drive using an MS-DOS filesystem, and /mnt/exta might be the same with an ext2 filesystem).
(系统管理员临时挂载的挂载点。程序不应该自动挂载在/mnt上。/mnt可能被划分为多个子目录（例如，/mnt/dosa可能是使用MS-DOS文件系统的软盘驱动器，而/mnt/exta可能是使用ext2文件系统的软盘驱动器）。)

/proc, /usr, /var, /home
Mount points for the other filesystems. Although /proc does not reside on any disk in reality it is still mentioned here. See the section about /proc later in the chapter.
```

via: [https://tldp.org/LDP/sag/html/root-fs.html](https://tldp.org/LDP/sag/html/root-fs.html)

