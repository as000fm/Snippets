> Tell mem ore about this: For something equivalent to "the whole workspace open at once," use a multi-root workspace (`.code-workspace` file listing each project folder). That gives you the same all-projects-visible-in-one-window experience as Eclipse's workspace, without needing Eclipse's metadata.

A multi-root workspace is just a JSON file that tells VS Code (or Windsurf/Antigravity/Cursor) "treat these separate folders as one logical session." Here's the mechanics.

**The file itself**

It's a `.code-workspace` file, plain JSON, you can hand-write or generate it. For your setup with several plain-Java and Maven projects scattered around:

```json
{
  "folders": [
    { "name": "common-lib", "path": "C:/dev/projects/common-lib" },
    { "name": "board-game-app", "path": "C:/dev/projects/board-game-app" },
    { "name": "word-grid-generator", "path": "C:/dev/projects/word-grid-generator" },
    { "name": "svg-diagram-generator", "path": "C:/dev/projects/svg-diagram-generator" }
  ],
  "settings": {
    "java.configuration.maven.userSettings": "C:/dev/.m2/settings.xml",
    "java.compile.nullAnalysis.mode": "automatic"
  },
  "extensions": {
    "recommendations": [
      "vscjava.vscode-java-pack"
    ]
  }
}
```

Save it anywhere (e.g. `all-projects.code-workspace`), then "File → Open Workspace from File." Every folder you listed shows up in the Explorer sidebar as its own root, side by side — visually similar to Eclipse's Project Explorer with multiple projects loaded.

**How the Java extension handles it**

This is the part that matters for you. The Java extension (JDT Language Server underneath) scans every root folder independently for `pom.xml` files and imports each as its own Maven-managed Java project — so four folders with four separate `pom.xml`s become four separate Java projects in the language server's model, each with its own classpath, each fully cross-referenceable (go-to-definition works across them if one declares a dependency on another via Maven coordinates, same as it would in Eclipse with M2Eclipse).

If a folder has no `pom.xml` but does have `.project`/`.classpath` (old Eclipse-style), JDT LS picks those up too. Mixed plain-Java and Maven folders in the same workspace work fine — each is just classified on its own terms.

**Settings scoping — the part people get wrong**

`.code-workspace` files have a `settings` block at the workspace level, but each individual folder can still carry its own `.vscode/settings.json` for folder-specific overrides (e.g. a different `java.configuration.runtimes` JDK pin for a legacy module still on Java 11 vs. your migration target on Java 21). Workspace-level settings apply globally unless a folder overrides them — useful for exactly your two-tier Tomcat 9/Java 11 → Tomcat 10.1/Java 21 situation if you keep both versions checked out side by side during the migration.

**Adding/removing folders**

You don't need to hand-edit the JSON every time — "File → Add Folder to Workspace" appends one, and right-click → "Remove Folder from Workspace" drops one, both rewriting the `.code-workspace` file for you automatically.

**Practical gotchas**

- Each Maven project's `.m2` resolution still happens independently per folder — there's no single shared "workspace-level" dependency cache concept like Eclipse's m2e workspace resolution (where a sibling project's unpublished local changes are picked up live without installing to `.m2` first). If you need that, you'd want a Maven **reactor** (parent `pom.xml` with `<modules>`) rather than a `.code-workspace` of independent projects — those are different problems even though both look like "many projects, one window."
- Large numbers of roots (dozens+) can slow down file watching and search indexing, especially over a network drive or via WSL's 9p filesystem bridge if you're crossing the Windows/Linux boundary — worth keeping it to the cluster of projects you're actively touching rather than everything you've ever written.
- The `.code-workspace` file itself is just a config file — commit it to a personal dotfiles repo if you want it portable across machines, but it's not meant to live inside any one project's own repo.

For your actual Java migration project specifically, since Tomcat 9/Jersey 2.x and Tomcat 10.1/Jersey 3.1.x are likely two branches or two checkouts of the same codebase rather than separate Maven modules, a multi-root workspace pointing at both checkouts side by side would let you diff/compare files across the migration boundary directly in the editor — which might be more useful to you day-to-day than the "many independent projects" use case it's originally designed for.

> So basically, I could copy all the java projects (maven or plain) I need into a folder and include a `.code-workspace`  file at the root of that directory that I would open the file using "File → Open Workspace from File" in VS Code (or Windsurf), right? Also, can the path in the JSON `.code-workspace` be relative instead of absolute?
>
> Mostly right, with one correction worth making before you actually do it.

**On copying projects into one folder — you don't need to**

You can, but it's not required and it adds a maintenance cost: now you've got duplicate copies to keep in sync with wherever your real working copies live (SVN-controlled, Gitea repos, etc.). Multi-root workspaces are specifically designed *not* to require that — the whole point is pointing at folders wherever they already sit on disk, no copying. Your projects could be scattered across `C:\dev\projects\foo`, `D:\other\bar`, or even a Linux path if you're working through WSL — the `.code-workspace` file just needs the paths.

If your actual goal is "I want one tidy folder I can hand off or zip up," then yes, copying plus a `.code-workspace` inside it works fine. But if your goal is just "I want all these to show up together in one editor window," skip the copy step and point the workspace file directly at your real project locations.

**Relative paths — yes, and this is the better default**

Relative paths are resolved relative to the location of the `.code-workspace` file itself, not your current directory or VS Code's last-used folder. So if you do go with the copy-into-one-folder approach, this:

```json
{
  "folders": [
    { "name": "common-lib", "path": "common-lib" },
    { "name": "board-game-app", "path": "board-game-app" },
    { "name": "word-grid-generator", "path": "word-grid-generator" }
  ]
}
```

with the `.code-workspace` file sitting at the root next to those three subfolders, works identically to the absolute-path version — and now it's portable. You could commit it to a repo, move the whole tree to another machine, or hand it to a teammate, and it still resolves correctly since nothing is hardcoded to `C:\dev\...`.

**Where relative paths actually pay off for your setup**

Given you bounce between Windows and Linux (WSL, or your Proxmox/server-side work), relative paths sidestep the `C:\` vs `/home/...` path-format mismatch entirely. Absolute paths baked into a `.code-workspace` would break the moment you open the same file from the other OS; relative paths just work as long as the folder structure underneath stays the same shape.

One thing relative paths *can't* do: reach outside the workspace file's own directory tree upward arbitrarily is fine (`../sibling-project` works), but if your real projects are genuinely scattered across unrelated drives with no common parent, relative paths can get awkward fast (`../../../other-drive/...`), and at that point absolute paths are simpler to reason about even though less portable.
